document.addEventListener('DOMContentLoaded', function() {
    // 탭 전환 기능
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    const tabSlider = document.querySelector('.tab-slider-indicator');
    
    // 초기 탭 슬라이더 위치 설정
    if (tabSlider) {
        const activeTab = document.querySelector('.tab-btn.active');
        const activeIndex = Array.from(tabBtns).indexOf(activeTab);
        updateTabSlider(activeIndex);
    }
    
    function updateTabSlider(activeIndex) {
        // 탭이 2개인 경우 0 또는 1의 인덱스를 가짐
        // 0일 때는 0%, 1일 때는 50%로 이동
        const position = activeIndex * 100;
        tabSlider.style.transform = `translateX(${position}%)`;
    }
    
    tabBtns.forEach((btn, index) => {
        btn.addEventListener('click', function() {
            // 탭 버튼 활성화
            tabBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            // 탭 슬라이더 이동 - 반드시 인덱스로 이동
            updateTabSlider(index);
            
            // 탭 컨텐츠 활성화 - 애니메이션 효과 추가
            const tabId = this.getAttribute('data-tab');
            
            // 현재 활성화된 탭을 비활성화
            tabContents.forEach(content => {
                if (content.classList.contains('active')) {
                    // 페이드아웃 애니메이션
                    content.style.opacity = '0';
                    content.style.transform = 'translateY(10px)';
                    
                    setTimeout(() => {
                        content.classList.remove('active');
                        
                        // 새 탭을 활성화
                        const newContent = document.getElementById(tabId);
                        newContent.classList.add('active');
                        
                        // 페이드인 애니메이션 적용을 위해 약간의 지연 추가
                        setTimeout(() => {
                            newContent.style.opacity = '1';
                            newContent.style.transform = 'translateY(0)';
                        }, 50);
                    }, 200);
                }
            });
            
            // 메시지 초기화
            document.querySelectorAll('.message').forEach(msg => {
                msg.textContent = '';
                msg.className = 'message';
            });
        });
    });
    
    // 로그인 폼 제출
    const loginForm = document.getElementById('loginForm');
    const loginMessage = document.getElementById('login-message');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('login-username').value;
            const password = document.getElementById('login-password').value;
            const submitButton = this.querySelector('button[type="submit"]');
            
            // 로딩 상태 표시
            submitButton.disabled = true;
            submitButton.innerHTML = '로그인 중 <span class="loading"></span>';
            loginMessage.textContent = '';
            
            // API 요청 (실제 구현은 주석 처리)
            setTimeout(() => {
                // 임시 로그인 검증 (실제로는 서버에서 검증)
                if (username === 'test' && password === 'password') {
                    loginMessage.textContent = '로그인 성공!';
                    loginMessage.className = 'message success';
                    
                    // 성공 시 리다이렉션 (실제 구현에서는 메인 페이지로)
                    setTimeout(() => {
                        alert('로그인 성공! 메인 페이지로 이동합니다.');
                    }, 1000);
                } else {
                    loginMessage.textContent = '아이디 또는 비밀번호가 올바르지 않습니다.';
                    loginMessage.className = 'message error';
                    submitButton.disabled = false;
                    submitButton.textContent = '로그인';
                }
            }, 1000); // 서버 응답 시뮬레이션
        });
    }
    
    // 회원가입 폼 제출
    const registerForm = document.getElementById('registerForm');
    const registerMessage = document.getElementById('register-message');
    
    if (registerForm) {
        // 아이디 중복 체크
        const usernameField = document.getElementById('register-username');
        const usernameCheck = document.getElementById('username-check');
        
        usernameField.addEventListener('blur', function() {
            if (this.value.length >= 4) {
                // 로딩 상태 표시
                usernameCheck.textContent = '확인 중...';
                usernameCheck.style.color = '#7c8da0';
                
                // API 요청 시뮬레이션
                setTimeout(() => {
                    // 임시 중복 체크 (실제로는 서버에서 확인)
                    if (this.value === 'admin') {
                        usernameCheck.textContent = '이미 사용 중인 아이디입니다.';
                        usernameCheck.style.color = '#e35050';
                    } else {
                        usernameCheck.textContent = '사용 가능한 아이디입니다.';
                        usernameCheck.style.color = '#0c9a6f';
                    }
                }, 500); // 서버 응답 시뮬레이션
            } else if (this.value.length > 0) {
                usernameCheck.textContent = '아이디는 4자 이상이어야 합니다.';
                usernameCheck.style.color = '#e6a23c';
            } else {
                usernameCheck.textContent = '';
            }
        });
        
        // 폼 제출 이벤트
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('register-email').value;
            const name = document.getElementById('register-name').value;
            const username = document.getElementById('register-username').value;
            const password = document.getElementById('register-password').value;
            const birthdate = document.getElementById('register-birthdate').value;
            const submitButton = this.querySelector('button[type="submit"]');
            
            // 기본 유효성 검사
            if (password.length < 6) {
                registerMessage.textContent = '비밀번호는 6자 이상이어야 합니다.';
                registerMessage.className = 'message error';
                return;
            }
            
            if (birthdate.length !== 8 || !/^\d+$/.test(birthdate)) {
                registerMessage.textContent = '생년월일은 8자리 숫자로 입력해주세요.';
                registerMessage.className = 'message error';
                return;
            }
            
            // 로딩 상태 표시
            submitButton.disabled = true;
            submitButton.innerHTML = '처리 중 <span class="loading"></span>';
            registerMessage.textContent = '';
            
            // API 요청 시뮬레이션
            setTimeout(() => {
                registerMessage.textContent = '회원가입이 완료되었습니다. 로그인해주세요.';
                registerMessage.className = 'message success';
                submitButton.disabled = false;
                submitButton.textContent = '회원가입';
                
                // 폼 초기화
                registerForm.reset();
                
                // 로그인 탭으로 전환
                setTimeout(() => {
                    document.querySelector('.tab-btn[data-tab="login"]').click();
                }, 2000);
            }, 1500); // 서버 응답 시뮬레이션
        });
    }
});

