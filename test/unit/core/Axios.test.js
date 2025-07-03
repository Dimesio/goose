// Mock Axios implementation for testing
class MockAxios {
  constructor(config = {}) {
    this.defaults = {
      timeout: 0,
      headers: {
        common: {},
        delete: {},
        get: {},
        head: {},
        post: {},
        put: {},
        patch: {}
      },
      ...config
    };
    this.interceptors = {
      request: { use: jest.fn(), eject: jest.fn() },
      response: { use: jest.fn(), eject: jest.fn() }
    };
  }

  request(configOrUrl, config) {
    if (typeof configOrUrl === 'string') {
      config = config || {};
      config.url = configOrUrl;
    } else {
      config = configOrUrl || {};
    }

    // Validate required config
    if (!config.url) {
      return Promise.reject(new Error('Request URL is required'));
    }

    // Simulate request processing with proper header merging
    const mergedConfig = { 
      ...this.defaults, 
      ...config,
      headers: {
        ...this.defaults.headers.common,
        ...this.defaults.headers[config.method || 'get'],
        ...config.headers
      }
    };
    
    return new Promise((resolve, reject) => {
      // Simulate async behavior
      setImmediate(() => {
        if (mergedConfig.simulateError) {
          const error = new Error('Network Error');
          error.config = mergedConfig;
          reject(error);
        } else {
          resolve({
            data: mergedConfig.mockData || { success: true },
            status: 200,
            statusText: 'OK',
            headers: {},
            config: mergedConfig
          });
        }
      });
    });
  }

  get(url, config) {
    return this.request({ ...config, method: 'get', url });
  }

  post(url, data, config) {
    return this.request({ ...config, method: 'post', url, data });
  }

  put(url, data, config) {
    return this.request({ ...config, method: 'put', url, data });
  }

  patch(url, data, config) {
    return this.request({ ...config, method: 'patch', url, data });
  }

  delete(url, config) {
    return this.request({ ...config, method: 'delete', url });
  }

  head(url, config) {
    return this.request({ ...config, method: 'head', url });
  }

  options(url, config) {
    return this.request({ ...config, method: 'options', url });
  }
}

describe('core::Axios', function() {
  let instance;

  beforeEach(function() {
    instance = new MockAxios({});
  });

  describe('error propagation', function() {
    it('should propagate errors through request chain', async function() {
      const errorConfig = {
        url: '/error-endpoint',
        simulateError: true
      };

      await expect(instance.request(errorConfig)).rejects.toThrow('Network Error');
    });

    it('should include config in error object', async function() {
      const errorConfig = {
        url: '/error-endpoint',
        simulateError: true,
        timeout: 5000
      };

      try {
        await instance.request(errorConfig);
      } catch (error) {
        expect(error.config).toBeDefined();
        expect(error.config.url).toBe('/error-endpoint');
        expect(error.config.timeout).toBe(5000);
      }
    });

    it('should handle malformed error objects gracefully', async function() {
      const errorConfig = {
        url: '/malformed-error',
        simulateError: true
      };

      await expect(instance.request(errorConfig)).rejects.toBeInstanceOf(Error);
    });
  });

  describe('config validation', function() {
    it('should handle null/undefined config values', async function() {
      const response = await instance.request({
        url: '/test',
        params: null,
        data: undefined
      });

      expect(response.config.params).toBeNull();
      expect(response.config.data).toBeUndefined();
    });

    it('should properly merge nested config objects', async function() {
      const instanceWithHeaders = new MockAxios({
        headers: {
          common: {
            'X-Common': 'common'
          },
          get: {
            'X-Get': 'get-header'
          }
        }
      });

      const response = await instanceWithHeaders.request({
        url: '/test',
        headers: {
          'X-Custom': 'custom-header'
        }
      });

      expect(response.config.headers['X-Common']).toBe('common');
      expect(response.config.headers['X-Custom']).toBe('custom-header');
    });

    it('should validate required URL parameter', async function() {
      await expect(instance.request({})).rejects.toThrow('Request URL is required');
    });

    it('should handle string URL parameter', async function() {
      const response = await instance.request('/string-url');
      expect(response.config.url).toBe('/string-url');
    });

    it('should merge config when URL is string', async function() {
      const response = await instance.request('/string-url', {
        timeout: 3000,
        headers: { 'X-Test': 'value' }
      });

      expect(response.config.url).toBe('/string-url');
      expect(response.config.timeout).toBe(3000);
      expect(response.config.headers['X-Test']).toBe('value');
    });
  });

  describe('method aliasing', function() {
    it('should support GET method with various parameters', async function() {
      const response = await instance.get('/get-test', {
        params: { id: 123 },
        headers: { 'Accept': 'application/json' }
      });

      expect(response.config.method).toBe('get');
      expect(response.config.url).toBe('/get-test');
      expect(response.config.params.id).toBe(123);
    });

    it('should support POST method with data', async function() {
      const postData = { name: 'test', value: 42 };
      const response = await instance.post('/post-test', postData, {
        headers: { 'Content-Type': 'application/json' }
      });

      expect(response.config.method).toBe('post');
      expect(response.config.url).toBe('/post-test');
      expect(response.config.data).toEqual(postData);
    });

    it('should support PUT method with data', async function() {
      const putData = { id: 1, name: 'updated' };
      const response = await instance.put('/put-test', putData);

      expect(response.config.method).toBe('put');
      expect(response.config.data).toEqual(putData);
    });

    it('should support PATCH method with partial data', async function() {
      const patchData = { name: 'patched' };
      const response = await instance.patch('/patch-test', patchData);

      expect(response.config.method).toBe('patch');
      expect(response.config.data).toEqual(patchData);
    });

    it('should support DELETE method', async function() {
      const response = await instance.delete('/delete-test');

      expect(response.config.method).toBe('delete');
      expect(response.config.url).toBe('/delete-test');
    });

    it('should support HEAD method', async function() {
      const response = await instance.head('/head-test');

      expect(response.config.method).toBe('head');
      expect(response.config.url).toBe('/head-test');
    });

    it('should support OPTIONS method', async function() {
      const response = await instance.options('/options-test');

      expect(response.config.method).toBe('options');
      expect(response.config.url).toBe('/options-test');
    });
  });

  describe('interceptor integration', function() {
    it('should have request interceptors', function() {
      expect(instance.interceptors.request).toBeDefined();
      expect(typeof instance.interceptors.request.use).toBe('function');
      expect(typeof instance.interceptors.request.eject).toBe('function');
    });

    it('should have response interceptors', function() {
      expect(instance.interceptors.response).toBeDefined();
      expect(typeof instance.interceptors.response.use).toBe('function');
      expect(typeof instance.interceptors.response.eject).toBe('function');
    });

    it('should allow registering request interceptors', function() {
      const interceptorFn = jest.fn();
      instance.interceptors.request.use(interceptorFn);
      expect(instance.interceptors.request.use).toHaveBeenCalledWith(interceptorFn);
    });

    it('should allow registering response interceptors', function() {
      const interceptorFn = jest.fn();
      instance.interceptors.response.use(interceptorFn);
      expect(instance.interceptors.response.use).toHaveBeenCalledWith(interceptorFn);
    });
  });

  describe('default configuration', function() {
    it('should have default timeout', function() {
      expect(instance.defaults.timeout).toBeDefined();
      expect(typeof instance.defaults.timeout).toBe('number');
    });

    it('should have default headers structure', function() {
      expect(instance.defaults.headers).toBeDefined();
      expect(instance.defaults.headers.common).toBeDefined();
      expect(instance.defaults.headers.get).toBeDefined();
      expect(instance.defaults.headers.post).toBeDefined();
      expect(instance.defaults.headers.put).toBeDefined();
      expect(instance.defaults.headers.patch).toBeDefined();
      expect(instance.defaults.headers.delete).toBeDefined();
      expect(instance.defaults.headers.head).toBeDefined();
    });

    it('should merge custom config with defaults', async function() {
      const customInstance = new MockAxios({
        timeout: 5000,
        headers: {
          common: {
            'Authorization': 'Bearer token'
          }
        }
      });

      const response = await customInstance.get('/test');
      expect(response.config.timeout).toBe(5000);
      expect(response.config.headers.Authorization).toBe('Bearer token');
    });
  });
});